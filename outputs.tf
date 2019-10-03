output "mongodb_external_ip" {
  value = "${google_compute_instance.mongo-db.network_interface.0.access_config.0.nat_ip}"
}

output "web_external_ip" {
  value = "${google_compute_instance.web.network_interface.0.access_config.0.nat_ip}"
}

output "teamcity-ci_external_ip" {
  value = "${google_compute_instance.teamcity-ci.network_interface.0.access_config.0.nat_ip}"
}


output "mongo-db_internal_ip" {
  value = "${google_compute_instance.mongo-db.network_interface.0.network_ip}"
}

output "web_internal_ip" {
  value = "${google_compute_instance.web.network_interface.0.network_ip}"
}

output "teamcity-ci_internal_ip" {
  value = "${google_compute_instance.teamcity-ci.network_interface.0.network_ip}"
}