require 'yaml'
tests = 
  {
    'testOne'     => false,
    'testTwo'     => false,
    'testThree'     => false,
    'testFour'     => false,
    'testFive'     => false,
    'testSix'     => false,
    'testSeven'     => false,
    'testEight'     => false,
    'testNine'     => false,
    'testTen'     => false,
  } 
check = YAML.dump( tests)
puts "YAML:\n\n#{check}"

tests.each do |name, _| 
puts "@Test(enabled = true)"
puts "public void #{name}(){"
puts "}"
end