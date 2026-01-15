module Gcd
  # tag::snippet[]
  def self.gcd(a, b)
    b == 0 ? a.abs : # <.>
      gcd(b, a % b) # <.>
  end
  # end::snippet[]
end
