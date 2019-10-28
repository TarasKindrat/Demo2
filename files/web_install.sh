
Echo "Creating carts.service"

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

