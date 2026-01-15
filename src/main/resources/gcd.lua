function gcd(a,b)
  if b==0 then return math.abs(a) end -- <.>
  return gcd(b, a % b) -- <.>
end
