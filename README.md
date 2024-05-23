# oapenmemomanager

## db settings

The `export.content` field is a LONGTEXT field. In mysqld.cnf set the `max_allowed_packet` to a sufficiently high value (128M) to be able to write large outputs to this field.

After updating, restart mysql server:

`systemctl restart mysql`


