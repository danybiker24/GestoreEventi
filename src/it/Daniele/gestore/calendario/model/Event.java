package it.Daniele.gestore.calendario.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import it.Daniele.gestore.settings.model.AppSettings;

import java.io.Serializable;

public abstract class Event implements Serializable, Comparable<Event> {
	CompetitionType competitionType;
	private String title;
	private ZonedDateTime start;
	private ZonedDateTime finish;
	private String description;
	private Optional<String> moreInfo;
	AppSettings appSettings;
	
	private static Map<String,Supplier<? extends Event>> knownEventType;
	
	private static final long serialVersionUID = 1L;
	
	protected Event(String title, ZonedDateTime start, ZonedDateTime finish, String description, String moreInfo) {
		super();
		this.appSettings = new AppSettings();
		
		if(title == null || start == null || finish == null || description == null) throw new IllegalArgumentException("Invalid data");
		if(start.isAfter(finish)) throw new IllegalArgumentException("Invalid start - finish");
		
		this.title = title;
		this.start = start;
		this.finish = finish;
		this.description = description;
		this.moreInfo = Optional.ofNullable(moreInfo);
	}
	
	public Event(String title, ZonedDateTime start, ZonedDateTime finish, String description) {
		this(title,start,finish,description,null);
	}
	
	public CompetitionType getCompetitionType() {
		return this.competitionType;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		if(title == null) throw new IllegalArgumentException("Invalid title");
		
		this.title = title;
	}

	public ZonedDateTime getStart() {
		return this.start;
	}
	
	public void setStart(ZonedDateTime start) {
		if(start == null) throw new IllegalArgumentException("Invalid start");
		this.start = start;
	}
	
	public ZonedDateTime getFinish() {
		return this.finish;
	}
	
	public void setFinish(ZonedDateTime finish) {
		if(finish == null) throw new IllegalArgumentException("Invalid finish");
		this.finish = finish;
	}
	
	public Duration getDuration() {
		return Duration.between(this.getStart(), this.getFinish());
	}
	
	public String getDescription() {	//gara, qualifica ecc.
		return this.description;
	}
	
	public void setDescription(String description) {
		if(description == null) throw new IllegalArgumentException("Invalid description");
		
		this.description = description;
	}
	
	public Optional<String> getMoreInfo() {	//Altre info
		return this.moreInfo;
	}
	
	public void setMoreInfo(String moreInfo) {
		if(moreInfo == null) throw new IllegalArgumentException("Invalid info");
		
		this.moreInfo = Optional.ofNullable(moreInfo);
	}
	
	public EventStatus getStatus() {
		if(ZonedDateTime.now().isBefore(this.getStart())) return EventStatus.TO_START;
		else if(ZonedDateTime.now().isAfter(this.getFinish())) return EventStatus.FINISHED;
		else return EventStatus.IN_PROGRESS;
	}
	
	public String print() {
		StringBuilder result = new StringBuilder();
		DateTimeFormatter dtF = DateTimeFormatter.ofLocalizedDateTime(this.appSettings.getFormatStyleStandardPrint()).withLocale(this.appSettings.getStandardLocalePrint());
		
		result.append("Title:\t");
		result.append(this.getTitle());
		result.append("\nStart:\t");
		result.append(dtF.format(this.getStart()));
		result.append("\nFinish:\t");
		result.append(dtF.format(this.getFinish()));
		result.append("\n");
		result.append(this.getStatus().toString());
		result.append("\n\n");
		
		result.append(this.getDescription());
		
		result.append("\n\n");
		
		if(this.getMoreInfo().isPresent()) result.append(this.getMoreInfo().get());
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append(this.getTitle());
		result.append(":\t");
		result.append(this.getDescription());
		result.append("  -  ");
		result.append(this.getStatus().toString());
		
		return result.toString();
	}
	
	@Override
	public int compareTo(Event that) {
		Comparator<Event> cmp = Comparator.comparing(Event::getStart)
										  .thenComparing(Event::getFinish)
										  .thenComparing(Event::getTitle);
		
		return cmp.compare(this, that);
	}
	
	public static Event getCorrectEventByName(String name) {
		if(knownEventType == null) knownEventType = new TreeMap<>();
		ZonedDateTime now = ZonedDateTime.now();
		
		knownEventType.put("motogp", () -> new MotoGPEvent("",now,now,""));
		knownEventType.put("formula1", () -> new Formula1Event("",now,now,""));
		knownEventType.put("mxgp", () -> new MXGPEvent("",now,now,""));
		knownEventType.put("sbk", () -> new SBKEvent("",now,now,""));
		knownEventType.put("mondiali", () -> new MondialiEvent("", now, now, ""));
		
		Supplier<? extends Event> result = null;
		
		result = knownEventType.get(name.toLowerCase());
		
		if(result == null) throw new IllegalArgumentException("Invalid event name");
		
		return result.get();
	}
}
