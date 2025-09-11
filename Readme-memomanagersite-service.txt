# In /etc/systemd/system create a file named oapen-memo-manager-website.service with the following content:
# https://www.baeldung.com/linux/run-java-application-as-service

[Unit]
Description=OAPEN MEMO Manager Website
After=syslog.target network.target

[Service]
SuccessExitStatus=143
User=oapen
Group=oapen

Type=simple

# ExecStart=java -Xmx512m -jar /home/oapen/oapenmemo/manager/manager.jar - does not find external config
# Call the runnable jar directly so Spring will find any external application.properties
# Optionally you can put a file manager.conf next to manager.jar with jvm args JAVA_OPTS="-Xms512m"
ExecStart=/home/oapen/oapenmemo/manager/manager.jar
ExecStop=/bin/kill -15 $MAINPID

Restart=always
RestartSec=5s

[Install]
WantedBy=multi-user.target

