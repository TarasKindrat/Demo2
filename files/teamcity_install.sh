webIP="$1";
export webIP;
# Cheking and set IP web
sudo cat <<EOF > /opt/cronweb.sh 
#!/bin/sh
if [ -n "$webIP" ]
then
    echo "$webIP" > /opt/webIP.txt  
elif [ -z "$webIP" ]
then 
    export webIP=$(head -n 1 /opt/webIP.txt)
else 
    echo "Wrong parameter"
fi    
EOF

sudo chmod +x cronweb.sh
# Set cron job to export IP cronweb.sh 
sudo cat <<EOF >/var/spool/cron/crontabs/root
@reboot /bin/bash /opt/cronweb.sh >>/opt/log/exportWebIP.log 2>&1
EOF

# Install java
echo "Installing wget and Java opedjdk-devel";
sudo yum -y install wget;
sudo yum install java-1.8.0-openjdk-devel;

#echo "Expotr HOME-variables for jre and java end new PATH"
#export $(/usr/bin/env java -XshowSettings:properties -version 2>&1 | grep "java.home" | sed -e 's/java.home/JRE_HOME/;s/ //g;')
#export $(/usr/bin/env java -XshowSettings:properties -version 2>&1 | grep "java.home" | sed -e 's/java.home/JAVA_HOME/;s/ //g;'| rev | cut -c 5- | rev)
#export PATH=$PATH:$(echo "$JAVA_HOME/bin"):$(echo "$JRE_HOME/bin")

# Download Teamcity
echo "Downloading Teamcity";
wget https://download.jetbrains.com/teamcity/TeamCity-2019.1.3.tar.gz;
tar xvf TeamCity-2019.1.3.tar.gz;
sudo mkdir /opt/teamcity;
sudo mv TeamCity /opt/teamcity;
#Delete folder with Teamcity in home directory
sudo rm -R TeamCity;
# Create unit teamsity
echo "Create  systemd unit teamsity";
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
echo "statring Teamcity";
sudo systemctl enable teamcity;
sudo systemctl start teamcity;
echo "Teamcity  are ready to use!"



