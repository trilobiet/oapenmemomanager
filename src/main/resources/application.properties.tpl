
spring.datasource.url=jdbc:mysql://12.34.56.123:3306/oapen_memo?reconnect=true&rewriteBatchedStatements=true
spring.datasource.username=trilobiet
spring.datasource.password=******

logging.level.root=INFO
logging.level.oapen.memoproject.manager=INFO
logging.file.name=${user.home}/memoproject/manager.log

# timeout session after 24 hours
server.servlet.session.timeout=1440m