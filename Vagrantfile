Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.provider "virtualbox" do |v|
    v.memory = 4048
    v.cpus = 2
  end
  
  # postgresql
  config.vm.network "forwarded_port", guest: 5432, host: 5432
  
  # ark node api
  config.vm.network "forwarded_port", guest: 4001, host: 4001

  # eth rpc
  config.vm.network "forwarded_port", guest: 8545, host: 8545
end
