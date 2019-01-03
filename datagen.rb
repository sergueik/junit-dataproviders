require 'yaml'

check = YAML.dump([
  {
    'row'     => 1,
    'keyword' => 'testng',
    'count'   => 42
  },
  {
    'row'     => 2,
    'keyword' => 'junit',
    'count'   => 71
  }
])
puts "YAML:\n\n#{check}"
