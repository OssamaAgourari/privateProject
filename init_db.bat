@echo off
echo Creating database schema...
"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -pabdo1234 < schema.sql
echo Initializing data...
"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -pabdo1234 < init_data.sql
echo Done!
pause 