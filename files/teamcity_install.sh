sudo yum -y install wget;
sudo yum -y install mc;
# install Ansible
echo "Install Ansible";
sudo yum -y install epel-release;
sudo yum -y install ansible;
echo $(ansible --version);

# Install java
echo "Installing wget and Java opedjdk-devel";
sudo yum -y install java-1.8.0-openjdk-devel;

#echo "install latest git versin"
#sudo yum -y groupinstall "Development Tools";
#sudo yum -y install gettext-devel openssl-devel perl-CPAN perl-devel zlib-devel;
#sudo yum -y install https://centos7.iuscommunity.org/ius-release.rpm;
## remove old version if exist
#sudo yum -y remove git;
#sudo yum -y install git2u-all;

echo "Expotr HOME-variables for jre and java end new PATH"
#export $(/usr/bin/env java -XshowSettings:properties -version 2>&1 | grep "java.home" | sed -e 's/java.home/JRE_HOME/;s/ //g;')
#export $(/usr/bin/env java -XshowSettings:properties -version 2>&1 | grep "java.home" | sed -e 's/java.home/JAVA_HOME/;s/ //g;'| rev | cut -c 5- | rev)
#export PATH=$PATH:$(echo "$JAVA_HOME/bin"):$(echo "$JRE_HOME/bin")
export $(/usr/bin/env java -XshowSettings:properties -version 2>&1 | grep "java.home" | sed -e 's/java.home/JAVA_HOME/;s/ //g;')
export PATH=$PATH:$(echo "$JAVA_HOME/bin")
# Download Teamcity
echo "Downloading Teamcity";
wget https://download.jetbrains.com/teamcity/TeamCity-2019.1.4.tar.gz;
tar xvf TeamCity-2019.1.4.tar.gz;
sudo mkdir /opt/teamcity;
sudo mv TeamCity /opt/teamcity;
#Delete folder with Teamcity in home directory
sudo rm TeamCity-2019.1.4.tar.gz;
# Create unit teamcity
echo "Create  systemd unit teamcity";
sudo touch /etc/systemd/system/teamcity.service;
sudo cat <<EOF > /etc/systemd/system/teamcity.service
[Unit]
Description=TeamCity Server
After=network.target

[Service]
Type=forking
PIDFile=/opt/teamcity/TeamCity/logs/teamcity.pid
ExecStart=/opt/teamcity/TeamCity/bin/runAll.sh start
ExecStop=/opt/teamcity/TeamCity/bin/runAll.sh stop


[Install]
WantedBy=multi-user.target
EOF

# No cheking ssh keys inside network
sudo cat <<EOF >> /etc/ssh/ssh_config
Host 10.156.0.*
   StrictHostKeyChecking no
   UserKnownHostsFile=/dev/null
EOF
sudo systemctl restart sshd

echo "statring Teamcity";
sudo systemctl enable teamcity;
sudo systemctl start teamcity;
echo "Teamcity  are ready to use!"
