create (s:Station {id:42, load:1, switch:"on"});
create (s:Station {id:1, load:2, switch:"on"});
create (s:Station {id:2, load:3, switch:"on"});
create (s:Station {id:3, load:7, switch:"on"});
create (s:Station {id:4, load:5, switch:"on"});
create (s:Station {id:5, load:1, switch:"off"});
create (s:Station {id:6, load:4, switch:"on"});
create (s:Station {id:7, load:6, switch:"on"});
create (s:Station {id:8, load:2, switch:"off"});
create (s:Station {id:9, load:3, switch:"on"});

match (sp:Station {id:42}),(sc:Station {id:1}) merge (sp)<-[:CABLE {capacity:5}]-(sc);
match (sp:Station {id:1}),(sc:Station {id:2}) merge (sp)<-[:CABLE {capacity:3}]-(sc);
match (sp:Station {id:2}),(sc:Station {id:3}) merge (sp)<-[:CABLE {capacity:7}]-(sc);
match (sp:Station {id:1}),(sc:Station {id:4}) merge (sp)<-[:CABLE {capacity:8}]-(sc);
match (sp:Station {id:42}),(sc:Station {id:5}) merge (sp)<-[:CABLE {capacity:10}]-(sc);
match (sp:Station {id:5}),(sc:Station {id:6}) merge (sp)<-[:CABLE {capacity:3}]-(sc);
match (sp:Station {id:5}),(sc:Station {id:7}) merge (sp)<-[:CABLE {capacity:2}]-(sc);
match (sp:Station {id:7}),(sc:Station {id:8}) merge (sp)<-[:CABLE {capacity:7}]-(sc);
match (sp:Station {id:8}),(sc:Station {id:9}) merge (sp)<-[:CABLE {capacity:10}]-(sc);

