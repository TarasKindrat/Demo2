# Bash script for creating carts service
sudo cat <<EOF > /opt/carts.sh 
#!/bin/sh
if [ "$1"=="start" ]
then
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
Description=Carts container
Requires=docker.service
After=docker.service
After=network.target

[Service]
Restart=always
ExecStart=/usr/bin/docker start carts
ExecStop=/usr/bin/docker stop carts

[Install]
WantedBy=default.target
EOF

echo "Web for carts has been configured, you can check it on web:8081 "

