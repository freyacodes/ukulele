src_dir=C:/Users/Justin/Projects/ukulele/tmp/upgradeH2/old-databases
dest_dir=C:/Users/Justin/Projects/ukulele/tmp/upgradeH2/new-databases

wget https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar
wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar
for filepath in $src_dir/*.mv.db; do
    dbname=$(basename "$filepath" .mv.db)
    
    # Export data from old db file to backup.zip
    echo "Exporting database..."
    java -cp h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:$src_dir/$dbname -script backup.zip -options compression zip
    rm -f $dest_dir/$dbname.mv.db
# Import data from the backup.zip to the new db file
    echo "Importing data..."
    java -cp h2-2.1.214.jar org.h2.tools.RunScript -url jdbc:h2:$dest_dir/$dbname -script ./backup.zip -options compression zip
    rm -f backup.zip
    echo "$dbname migrated succesfully"
done
rm -f h2-1.4.200.jar
rm -f h2-2.1.214.jar
