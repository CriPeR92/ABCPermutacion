/*
 *
 * Copyright (c) 2004-2008 Arizona State University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ARIZONA STATE UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package edu.asu.emit.qyan.alg.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Pair;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.QYPriorityQueue;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseGraph;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

/**
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision: 673 $
 * @latest $Id: YenTopKShortestPathsAlg.java 673 2009-02-05 08:19:18Z qyan $
 */
public class YenTopKShortestPathsAlg {
    private VariableGraph _graph = null;

    // intermediate variables
    List<Path> _result_list = new Vector<Path>();
    Map<Path, BaseVertex> _path_derivation_vertex_index = new HashMap<Path, BaseVertex>();
    QYPriorityQueue<Path> _path_candidates = new QYPriorityQueue<Path>();

    /**
     * Default constructor.
     *
     * @param graph
     * @param k
     */
    public YenTopKShortestPathsAlg(BaseGraph graph) {
        this._graph = new VariableGraph((Graph) graph);
    }

    /**
     * Clear the variables of the class.
     */
    public void clear() {
        _path_candidates = new QYPriorityQueue<Path>();
        _path_derivation_vertex_index.clear();
        _result_list.clear();
    }

    /**
     * @param source
     * @param sink
     * @param top_k
     * @return
     */
    public List<Path> get_shortest_paths(BaseVertex source_vertex,
                                         BaseVertex target_vertex, int top_k) {
        DijkstraShortestPathAlg dijkstra_alg = new DijkstraShortestPathAlg(_graph);
        //1. achieve the shortest path in the graph from source to target
        Path shortest_path = dijkstra_alg.get_shortest_path(source_vertex, target_vertex);

        //2. initialize the variables
        clear();
        int count = 0;

        _path_candidates.add(shortest_path);

        _path_derivation_vertex_index.put(shortest_path, source_vertex);

        //3. main loop of the algorithm
        while (!_path_candidates.isEmpty() && count < top_k) {
            //3.1 prepare for removing vertices and arcs
            Path cur_path = _path_candidates.poll();
            _result_list.add(cur_path);

            BaseVertex cur_derivation = _path_derivation_vertex_index.get(cur_path);

            ++count;

            //3.2 remove the vertices and arcs in the graph
            for (int i = 0; i < count - 1; ++i) {
                Path cur_result_path = _result_list.get(i);

                int cur_dev_vertex_id =
                        cur_result_path.get_vertices().indexOf(cur_derivation);

                BaseVertex cur_succ_vertex =
                        cur_result_path.get_vertices().get(cur_dev_vertex_id + 1);

                _graph.remove_edge(new Pair<Integer, Integer>(
                        cur_derivation.get_id(), cur_succ_vertex.get_id()));
            }

            int path_length = cur_path.get_vertices().size();
            List<BaseVertex> cur_path_vertex_list = cur_path.get_vertices();
            for (int i = 0; i < path_length - 1; ++i) {
                _graph.remove_vertex(cur_path_vertex_list.get(i).get_id());
                _graph.remove_edge(new Pair<Integer, Integer>(
                        cur_path_vertex_list.get(i).get_id(),
                        cur_path_vertex_list.get(i + 1).get_id()));
            }

            //3.3 calculate the shortest tree rooted at target vertex in the graph
            DijkstraShortestPathAlg reverse_tree = new DijkstraShortestPathAlg(_graph);
            reverse_tree.get_shortest_path_flower(target_vertex);

            //3.4 recover the deleted vertices and update the cost and identify the new candidate results
            boolean is_done = false;
            for (int i = path_length - 2; i >= 0 && !is_done; --i) {
                //3.4.1 get the vertex to be recovered
                BaseVertex cur_recover_vertex = cur_path_vertex_list.get(i);
                _graph.recover_removed_vertex(cur_recover_vertex.get_id());

                //3.4.2 check if we should stop continuing in the next iteration
                if (cur_recover_vertex.get_id() == cur_derivation.get_id()) {
                    is_done = true;
                }

                //3.4.3 calculate cost using forward star form
                Path sub_path = reverse_tree.update_cost_forward(cur_recover_vertex);

                //3.4.4 get one candidate result if possible
                if (sub_path != null) {
                    //3.4.4.1 get the prefix from the concerned path
                    double cost = 0;
                    List<BaseVertex> pre_path_list = new Vector<BaseVertex>();
                    reverse_tree.correct_cost_backward(cur_recover_vertex);

                    for (int j = 0; j < path_length; ++j) {
                        BaseVertex cur_vertex = cur_path_vertex_list.get(j);
                        if (cur_vertex.get_id() == cur_recover_vertex.get_id()) {
                            j = path_length;
                        } else {
                            cost += _graph.get_edge_weight_of_graph(cur_path_vertex_list.get(j),
                                    cur_path_vertex_list.get(j + 1));
                            pre_path_list.add(cur_vertex);
                        }
                    }
                    pre_path_list.addAll(sub_path.get_vertices());

                    //3.4.4.2 compose a candidate
                    sub_path.set_weight(cost + sub_path.get_weight());
                    sub_path.get_vertices().clear();
                    sub_path.get_vertices().addAll(pre_path_list);

                    //3.4.4.3 put it in the candidate pool if new
                    if (!_path_derivation_vertex_index.containsKey(sub_path)) {
                        _path_candidates.add(sub_path);
                        _path_derivation_vertex_index.put(sub_path, cur_recover_vertex);
                    }
                }

                //3.4.5 restore the edge
                BaseVertex succ_vertex = cur_path_vertex_list.get(i + 1);
                _graph.recover_removed_edge(new Pair<Integer, Integer>(
                        cur_recover_vertex.get_id(), succ_vertex.get_id()));

                //3.4.6 update cost if necessary
                double cost_1 = _graph.get_edge_weight(cur_recover_vertex, succ_vertex)
                        + reverse_tree.get_start_vertex_distance_index().get(succ_vertex);

                if (reverse_tree.get_start_vertex_distance_index().get(cur_recover_vertex) > cost_1) {
                    reverse_tree.get_start_vertex_distance_index().put(cur_recover_vertex, cost_1);
                    reverse_tree.get_predecessor_index().put(cur_recover_vertex, succ_vertex);
                    reverse_tree.correct_cost_backward(cur_recover_vertex);
                }
            }

            //3.5 restore everything
            _graph.recover_removed_edges();
            _graph.recover_removed_vertices();
        }

        return _result_list;
    }

    /**
     * Return the list of results generated on the whole.
     * (Note that some of them are duplicates)
     *
     * @return
     */
    public List<Path> get_result_list() {
        return _result_list;
    }

    /**
     * The number of distinct candidates generated on the whole.
     *
     * @return
     */
    public int get_cadidate_size() {
        return _path_derivation_vertex_index.size();
    }

}
