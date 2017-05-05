package client;

public class InputParameters {
	// <event_filename> <snapshot_filename> <retrieval node> <state>
	
	String event_filename;
	String snapshot_filename;
	String retrieval_node;
	String state;
	
	public InputParameters(String event, String snapshot, String retreival, String state) {
		this.event_filename = event;
		this.snapshot_filename = snapshot + retreival;
		this.retrieval_node = retreival;
		this.state = state;
	}

}
