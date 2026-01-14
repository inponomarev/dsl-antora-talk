package org.example;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class GCD {
    public enum Lang {
        JAVA, RUBY, LUA, PROLOG
    }

    //@formatter:off
// tag::javagcd[]
private static long gcdJava(long a, long b) {
    return b == 0 ? Math.abs(a) : gcdJava(b, a % b);
}
// end::javagcd[]
//@formatter:on

    private static final ThreadLocal<ScriptingContainer> JRUBY =
            ThreadLocal.withInitial(() -> {
                ScriptingContainer c =
                        new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT);
                c.runScriptlet(readUtf8("gcd.rb"));
                return c;
            });

    private static long gcdRuby(long a, long b) {
        ScriptingContainer c = JRUBY.get();
        Object gcdModule = c.runScriptlet("Gcd");
        return (Long) c.callMethod(gcdModule, "gcd", a, b);
    }

    private static final ThreadLocal<Globals> LUA =
            ThreadLocal.withInitial(() -> {
                Globals g = JsePlatform.standardGlobals();
                g.load(readUtf8("gcd.lua")).call();
                return g;
            });

    private static long gcdLua(long a, long b) {
        Globals g = LUA.get();
        LuaValue fn = g.get("gcd");
        return fn.call(LuaValue.valueOf(a), LuaValue.valueOf(b)).tolong();
    }

    private static final ThreadLocal<Prolog> PROLOG =
            ThreadLocal.withInitial(() -> {
                Prolog p = new Prolog();
                try {
                    p.setTheory(new Theory(readUtf8("gcd.pl")));
                } catch (Exception e) {
                    throw new ExceptionInInitializerError(e);
                }
                return p;
            });

    private static long gcdProlog(long a, long b) {
        try {
            Prolog p = PROLOG.get();
            SolveInfo info = p.solve("gcd(" + a + "," + b + ",G).");
            if (!info.isSuccess()) {
                throw new IllegalStateException("No solution for gcd(" + a + "," + b + ",G).");
            }
            Term g = info.getVarValue("G");
            return Long.parseLong(g.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readUtf8(String resourcePath) {
        // Allow both "gcd.rb" and "/gcd.rb"
        String normalized = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;

        try (InputStream in = GCD.class.getResourceAsStream(normalized)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found on classpath: " + normalized);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + normalized, e);
        }
    }

    static long gcd(long a, long b, Lang lang) {
        return switch (lang) {
            case JAVA -> gcdJava(a, b);
            case RUBY -> gcdRuby(a, b);
            case LUA -> gcdLua(a, b);
            case PROLOG -> gcdProlog(a, b);
        };
    }
}
