echo "install latest git versin"
sudo yum -y groupinstall "Development Tools";
sudo yum -y install gettext-devel openssl-devel perl-CPAN perl-devel zlib-devel;
sudo yum -y install https://centos7.iuscommunity.org/ius-release.rpm;
# remove old version if exist
sudo yum -y remove git;
sudo yum -y install git2u-all;

echo "Creating carts.service"

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
sudo systemctl daemon-reload;
echo "Web for carts has been configured, you can check it on web:8081 "

