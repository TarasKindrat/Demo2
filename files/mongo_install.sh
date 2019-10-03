# Get internal IP address
ipaddr=$(hostname -I);
echo "local ip address is $ipaddr";
# Add mongoDB repo
sudo touch /etc/yum.repos.d/mongodb-org-4.2.repo;
sudo cat <<EOF > /etc/yum.repos.d/mongodb-org-4.2.repo
[mongodb-org-4.2]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/7Server/mongodb-org/4.2/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.2.asc
EOF
# Install mongoDB
echo "Installing MongoDB";
sudo yum install -y mongodb-org;
# Add internal instance's IP to mongo config file
sudo sed -i "s/bindIp: 127.0.0.1/bindIp: 127.0.0.1 $ipaddr/" /etc/mongod.conf;
# Add to boot and start mongoDB
sudo systemctl enable mongod;
sudo systemctl start mongod;
echo "MongoDB are ready to use!"
