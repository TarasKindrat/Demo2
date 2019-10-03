# Get IP mongoDB from parametr
mongodb="$1"; 
#export mongodb;
externalIP="$2";
# Install java
echo "Installing Java opedjdk-devel";
sudo yum install java-1.8.0-openjdk-devel;

# Cheking and set IP mongoDB
if [ -n "$mongodb" ]
then
   sudo echo "$mongodb" > /opt/mongoIP.txt  
else 
    echo "Wrong parameter"
fi;    

# Bash script for creating carts service
sudo cat <<EOF > /opt/carts.sh 
#!/bin/sh
if [ "$1"=="start" ]
then
    mongodb=$(head -n 1 /opt/mongoIP.txt); 
    java -Ddb:carts-db="$mongodb" -jar /opt/carts.jar;
elif [ "$1"=="stop" ]
then 
    kill $(ps aux | grep *carts* | grep -v "grep" | tr -s " "| cut -d " " -f 2)
else 
    echo "Wrong parameter, start or stop expected"
fi    
EOF

sudo chmod +x /opt/carts.sh

sudo cat <<EOF > /etc/systemd/system/carts.service 
[Unit]
Description=Carts service
After=network.target

[Service]
Type=simple
ExecStart=/opt/carts.sh start
ExecStop=/opt/carts.sh stop
#ExecStop=/bin/kill -- $MAINPID
TimeoutStartSec=0

[Install]
WantedBy=default.target
EOF

echo "Web for carts has been configured, you can check it on $externalIP:8081 after CI"

