package game_logic;

public class Coordinates {

	final private int x;
	final private int y;

	Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return (x * 31) ^ y;
	}
	@Override
	public boolean equals(Object o){
		if (o instanceof Coordinates) {
			Coordinates other = (Coordinates) o;
			return (x == other.getX() && y == other.getY());
		}
		return false;
	}
}