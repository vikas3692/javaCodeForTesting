
public class ChainTask {
	private int chainId;
	private int currentTask;
	private int NextTask;
	private int orderBy;

	public ChainTask(int chainId, int currentTask, int nextTask, int orderBy) {
		super();
		this.chainId = chainId;
		this.currentTask = currentTask;
		NextTask = nextTask;
		this.orderBy = orderBy;
	}

	public ChainTask() {
		// TODO Auto-generated constructor stub
	}

	public int getChainId() {
		return chainId;
	}

	public void setChainId(int chainId) {
		this.chainId = chainId;
	}

	public int getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(int currentTask) {
		this.currentTask = currentTask;
	}

	public int getNextTask() {
		return NextTask;
	}

	public void setNextTask(int nextTask) {
		NextTask = nextTask;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public String toString() {
		return "ChainTask [chainId=" + chainId + ", currentTask=" + currentTask + ", NextTask=" + NextTask
				+ ", orderBy=" + orderBy + "]";
	}
}
