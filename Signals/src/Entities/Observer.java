package Entities;

public abstract class Observer {

	protected String assetA, assetB, symbol;

	public Observer(String assetA, String assetB) {
		this.assetA = assetA;
		this.assetB = assetB;
		symbol = String.format("%s%s", assetA, assetB).toLowerCase();
	}

	public abstract void update();

}
