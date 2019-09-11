package com.prodyna.demo.recursion;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.Node;
import org.neo4j.harness.junit.Neo4jRule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.neo4j.driver.v1.Values.parameters;

public class RecursionTest {

    // This rule starts a Neo4j instance
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            // This is the Procedure we want to test
            .withProcedure(Recursion.class);

    @Test
    public void createTestsDataAndVerifyManually() throws Throwable {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
            Session session = driver.session();
            createTestData(session);

            StatementResult result = session.run("match (root:Station) optional match (root)-[r:CABLE]->(:Station) with root,count(r) as rc where rc = 0 return root");
            assertTrue( result.hasNext() );
            Record record = result.next();
            System.out.println (record );
            NodeValue root = (NodeValue) record.get( 0 );
            int rootId = root.get("id").asInt();
            assertEquals( "ID of root node", 42, rootId );
            assertFalse (result.hasNext() );

        }
    }

    @Test
    public void testRecursionFindRoot() throws Throwable {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
            Session session = driver.session();
            createTestData(session);

            StatementResult result = session.run("call recursion.rootNode()");
            assertTrue( result.hasNext() );
            Record record = result.next();
            NodeValue root = (NodeValue) record.get(0);
            long rootId = root.get("id").asInt();
            assertEquals( "ID of root node", 42, rootId );
            assertFalse (result.hasNext() );
        }
    }

    @Test
    public void testRecursionEffectiveLoad() throws Throwable {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
            Session session = driver.session();
            createTestData(session);

            {
                StatementResult result = session.run("call recursion.effectiveLoad()");
                assertTrue(result.hasNext());
                Record record = result.next();
                NodeValue root = (NodeValue) record.get(0);
                System.out.println(root.asNode());
                int effectiveLoad = root.asNode().get("effectiveLoad").asInt();
                assertEquals("Effective load", 34, effectiveLoad);
                assertFalse(result.hasNext());
            }

            {
                StatementResult result = session.run("match (ps:Station)<-[c:CABLE]-(cs:Station) return cs.id as id, cs.effectiveLoad as load, c.capacity as capacity");
                while (result.hasNext()) {
                    Record r = result.next();
                    System.out.println( r );
                }
            }
        }
    }

    private void createTestData(Session session) {
        // And given I have a node in the database
        session.run("create (s:Station {id:42, load:1})");
        session.run("create (s:Station {id:1, load:2})");
        session.run("create (s:Station {id:2, load:3})");
        session.run("create (s:Station {id:3, load:7})");
        session.run("create (s:Station {id:4, load:5})");
        session.run("create (s:Station {id:5, load:1})");
        session.run("create (s:Station {id:6, load:4})");
        session.run("create (s:Station {id:7, load:6})");
        session.run("create (s:Station {id:8, load:2})");
        session.run("create (s:Station {id:9, load:3})");

        session.run("match (sp:Station {id:42}),(sc:Station {id:1}) merge (sp)<-[:CABLE {capacity:5}]-(sc)");
        session.run("match (sp:Station {id:1}),(sc:Station {id:2}) merge (sp)<-[:CABLE {capacity:3}]-(sc)");
        session.run("match (sp:Station {id:2}),(sc:Station {id:3}) merge (sp)<-[:CABLE {capacity:7}]-(sc)");
        session.run("match (sp:Station {id:1}),(sc:Station {id:4}) merge (sp)<-[:CABLE {capacity:8}]-(sc)");
        session.run("match (sp:Station {id:42}),(sc:Station {id:5}) merge (sp)<-[:CABLE {capacity:10}]-(sc)");
        session.run("match (sp:Station {id:5}),(sc:Station {id:6}) merge (sp)<-[:CABLE {capacity:3}]-(sc)");
        session.run("match (sp:Station {id:5}),(sc:Station {id:7}) merge (sp)<-[:CABLE {capacity:2}]-(sc)");
        session.run("match (sp:Station {id:7}),(sc:Station {id:8}) merge (sp)<-[:CABLE {capacity:7}]-(sc)");
        session.run("match (sp:Station {id:8}),(sc:Station {id:9}) merge (sp)<-[:CABLE {capacity:10}]-(sc)");
    }
}
