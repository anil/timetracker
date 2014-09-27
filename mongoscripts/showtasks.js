db = connect("localhost:27017/tasktracker");
printjson(db.getCollectionNames());
cursor = db.tasks.find();
while ( cursor.hasNext() ) {
   printjson( cursor.next() );
}
