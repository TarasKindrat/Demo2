// Configure the Google Cloud provider

provider "google" {
  credentials = var.credentials
  project     = var.project
  region      = var.region
}

resource "google_compute_instance" "mongo-db" {
  name         = "mongo-db"
  machine_type = var.machine_type
  zone         = var.zone
  tags         = ["mongo-db"]

  # definition of the boot disk - the initial image 
  boot_disk {
    initialize_params {
      image = var.disk_image
    }
  }

  network_interface {
    network            = var.network
    subnetwork         = var.subnetwork
    subnetwork_project = var.subnetwork_project
    network_ip         = var.network_ip
    
    access_config {
            nat_ip = var.nat_ip
    }
  }
 
 metadata = {
    ssh-keys = "${var.ssh_user}:${file(var.public_key_path)}"
  }

}


resource "google_compute_instance" "web" {
  depends_on = [google_compute_instance.mongo-db]
  name         = "web"
  machine_type = var.machine_type
  zone         = var.zone
  tags         = ["web"]

  # definition of the boot disk - the initial image 
  boot_disk {
    initialize_params {
      image = var.disk_image
    }
  }

  network_interface {
    network            = var.network
    subnetwork         = var.subnetwork
    subnetwork_project = var.subnetwork_project
    network_ip         = var.network_ip

    access_config {
            nat_ip  = var.nat_ip
    }
  }
  
  metadata = {
    ssh-keys = "${var.ssh_user}:${file(var.public_key_path)}"
  }

}

resource "google_compute_firewall" "allow-mongo" {
  depends_on = [google_compute_instance.mongo-db]
  name        = "allow-mongo"
  network     = var.network
  target_tags = ["mongo-db"]

  allow {
    protocol = "tcp"
    ports    = ["27017"]
  }

  allow {
    protocol = "icmp"
  }
}

resource "google_compute_firewall" "allow-http" {
  depends_on = [google_compute_instance.web]
  name        = "allow-http"
  network     = var.network
  target_tags = ["web"]

  allow {
    protocol = "tcp"
    ports    = ["80", "8080", "8081", "8089"]
  }

  allow {
    protocol = "icmp"
  }
}

#resource "google_compute_firewall" "allow-ssh" {
#  depends_on = [google_compute_instance.web, google_compute_instance.mongo-db]
#  name    = "ssh-firewall"
#  network = var.network

#  #target_tags = google_compute_instance.web.tags
#  target_tags = ["web", "mongo-db"]

#  allow {
#    protocol = "tcp"
#    ports    = ["22"]
#  }

#  allow {
#    protocol = "icmp"
#  }
#}


resource "null_resource" "provision" {
  
  depends_on = [google_compute_firewall.allow-http]

  provisioner "local-exec" {
    #command = "ansible-playbook -u taras -i '${self.public_ip},' --private-key ${var.private_key_path} provision.yml"
    command = "sleep 10; ansible-playbook provision_install_Docker.yml -vvvv" 
  }
} 

#resource "null_resource" "web_prov" {
# 
#  depends_on = [null_resource.provision]
#
## connection for the work of service providers after installing and configuring the OS
#  connection {
#    host        = "${google_compute_instance.web.network_interface.0.access_config.0.nat_ip}"
#    type        = "ssh"
#    user        = "${var.ssh_user}"
#    agent       = false
#    private_key = "${file(var.private_key_path)}"
#  }
#  
# # Copy sh provision script
#  provisioner "file" {
#    source      = "./files/web_install.sh"
#    destination = "/tmp/web_install.sh"   
# } 
#
#  provisioner "remote-exec" {
#  
#    inline = [
#      "sudo chmod +x /tmp/web_install.sh",
#      "sudo /bin/bash /tmp/web_install.sh"
#    ]
#  }
#}
