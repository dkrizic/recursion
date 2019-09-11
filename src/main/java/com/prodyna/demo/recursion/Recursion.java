package com.prodyna.demo.recursion;

import java.util.*;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.kernel.builtinprocs.BuiltInProcedures;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import static org.neo4j.procedure.Mode.READ;
import static org.neo4j.procedure.Mode.WRITE;

public class Recursion {

    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Procedure(name = "recursion.rootNode", mode = READ )
    public Stream<NodeResult> rootNode() {
        List<NodeResult> nodes = new ArrayList<>();
        final Result result = db.execute("MATCH (root:Station) OPTIONAL MATCH (root)-[r:PART_OF]->(:Station) WITH root,count(r) AS rc WHERE rc = 0 RETURN root");
        while( result.hasNext() ) {
            Node node = (Node) result.next().get("root");
            nodes.add( new NodeResult( node ));
        }
        return nodes.stream();
    }

    public class NodeResult {

        public final Node node;

        public NodeResult(Node node) {
            this.node = node;
        }
    }


}
