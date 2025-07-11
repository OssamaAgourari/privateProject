@echo off
echo Setting up database...
"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -u root -pabdo1234 < setup.sql
echo Done!
pause 