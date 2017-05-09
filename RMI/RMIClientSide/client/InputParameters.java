package client;

public class InputParameters {
	// <event_filename> <snapshot_filename> <retrieval node> <state>
	
	String event_filename;
	String snapshot_filename;
	String retrieval_node;
	String state;
	String watch;
	String gd_file;
	
	public InputParameters(String event, String snapshot, String retreival, String state, String watch, String gd_file) {
		this.event_filename = event;
		this.snapshot_filename = snapshot;
		this.retrieval_node = retreival;
		this.state = state;
		this.watch = watch;
		this.gd_file = gd_file;
	}

}
