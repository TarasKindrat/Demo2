# Demo2
- use Terraform from Teamcity to create infrastructure
- use Ansible to install Docker (Terraform provision)
- use Teamcity to build and run apps from microservices-demo 
For import some resource (example team-city):  terraform import google_compute_instance.teamcity-ci demo2-xxxxxxx/europe-west3-c/teamcity-ci 

After manual configuration of infrastructure terraform refresh will add changes to terraform.tfstate
