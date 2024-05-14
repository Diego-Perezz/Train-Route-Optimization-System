package main;

public class Station {
	
	String Name;
	int Distance;
	
	
	public Station(String name, int dist) {
		
		this.Distance = dist;
		this.Name = name;
		
	}
	
	public String getCityName() {
		return this.Name;
	}
	
	public void setCityName(String cityName) {
		this.Name = cityName;
	}
	
	public int getDistance() {
		return this.Distance;
	}
	
	public void setDistance(int distance) {
		this.Distance = distance;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Station other = (Station) obj;
		return this.getCityName().equals(other.getCityName()) && this.getDistance() == other.getDistance();
	}
	@Override
	public String toString() {
		return "(" + this.getCityName() + ", " + this.getDistance() + ")";
	}

}
