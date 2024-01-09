In /etc/systemd/system create a file named oapen-memo-manager-website.service with the following content:

[Unit]
Description=OAPEN MEMO Manager Website
After=syslog.target

[Service]
User=oapen
ExecStart=/home/oapen/oapenmemo/manager.jar SuccessExitStatus=143

[Install] 
WantedBy=multi-user.target
