package ksio.voteday.vote;

public enum VoteType {
	Day, Night, Rain, Sun;
	
	public Class<?> getVoteClass() {
		switch(this) {
			case Day:
				return VoteDay.class;
			case Rain:
				return Vote.class;
			default:
		}
		return null;
				
	}
}
