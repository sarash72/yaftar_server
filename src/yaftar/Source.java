package yaftar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "source")
public class Source {
	@Id
	@GeneratedValue
	@Column(name = "date")
	private String date;

	@Column(name = "time")
	private String time;

	@Column(name = "disk")
	private String disk;

	@Column(name = "memory")
	private String memory;

	@Column(name = "cpu")
	private String cpu;

	@Column(name = "IO")
	private String IO;

	Source(String date, String time, String disk, String memory, String cpu, String IO) {
		this.date = date;
		this.time = time;
		this.disk = disk;
		this.memory = memory;
		this.cpu = cpu;
		this.IO = IO;

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getIO() {
		return IO;
	}

	public void setIO(String iO) {
		IO = iO;
	}

}
