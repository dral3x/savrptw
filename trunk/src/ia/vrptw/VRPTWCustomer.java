package ia.vrptw;

public class VRPTWCustomer {

	int _id;
	double _position_x;
	double _position_y;
	double _demand;
	double _ready_time;
	double _due_date;
	double _service_time;
	
	public VRPTWCustomer(int id, double position_x, double position_y, double demand, double ready_time, double due_date, double service_time) {
		_id = id;
		_position_x = position_x;
		_position_y = position_y;
		_demand = demand;
		_ready_time = ready_time;
		_due_date = due_date;
		_service_time = service_time;
	}
	
	public int getID() {
		return _id;
	}
	
	public double getXPosition() {
		return _position_x;
	}
	
	public double getYPosition() {
		return _position_y;
	}
	
	public void show() {
		System.out.println(_id + "\t" + _position_x + "\t" + _position_y + "\t" + _demand + "\t"+ _ready_time + "\t" + _due_date + "\t" + _service_time);
	}
}
