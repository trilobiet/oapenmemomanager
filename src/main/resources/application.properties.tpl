
spring.datasource.url=jdbc:mysql://12.34.56.123:3306/oapen_memo?reconnect=true&rewriteBatchedStatements=true
spring.datasource.username=trilobiet
spring.datasource.password=******

logging.level.root=INFO
logging.level.oapen.memoproject.manager=INFO
logging.file.name=${user.home}/oapenmemo/logs/oapen_memo-manager.log

# timeout session after 24 hours
server.servlet.session.timeout=1440m

# spring.jpa.show-sql=true
# spring.jpa.properties.hibernate.format_sql=true