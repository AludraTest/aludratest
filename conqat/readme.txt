AludraTest Architecture Assessment
----------------------------------
AludraTests's architecture assessment has been implemented using ConQAT 2015.2.

Installation: 
- download the ConQAT Engine 2015.2 Binary Distribution from https://www.cqse.eu/en/products/conqat/install/
- unpack it
- set the environment variable CONQAT_HOME to the path where you unpacked it to
- on Unix and Mac systems set the access permissions on the executable chmod u+x CONQAT_HOME/bin*.sh
- add conqat.sh to the path

Performing the analysis
- Open a shell
- cd to the project directory
- conqat -f conqat/aludratest.architecture.cqr
- view the architecture assessment result in target/conqat/index.html
