package pg.share;

public enum SizeValuesType {
	BUYER_PO(1),
	SAMPLE_REQUISITION(2),
	SAMPLE_CUTTING(3),
	CUTTING(3),
	CUTTING_RATIO(4),
	CUTTING_QTY(5);
	private int type;
	private SizeValuesType(int type) {
		this.type = type;
	}	
	public int getType() {
		return type;
	}
}
