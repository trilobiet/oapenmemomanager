
server.port=8084

# timeout session after 24 hours
server.servlet.session.timeout=1440m

taskrunner.url=http://localhost:8085

spring.datasource.url=jdbc:mysql://12.34.56.123:3306/oapen_memo?reconnect=true&rewriteBatchedStatements=true
spring.datasource.username=trilobiet
spring.datasource.password=******

logging.file.name=${user.home}/[...]/logs/oapen_memo-manager.log
logging.level.root=INFO
logging.level.oapen.memoproject.manager=INFO
logging.logback.rollingpolicy.max-history=40

path.exports=${user.home}/oapen/oapenmemo/exports/

# timeout session after 24 hours
server.servlet.session.timeout=1440m

# spring.jpa.show-sql=true
# spring.jpa.properties.hibernate.format_sql=true