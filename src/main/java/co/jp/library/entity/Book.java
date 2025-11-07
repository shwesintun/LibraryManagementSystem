package co.jp.library.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
@Table(name = "books")
public class Book {
	@Id
    private String registrationId;
	
    private String bookName;
    private String bookCategory;
    private String author;
    private String producedYear;
    private String bookType;
    private String fileName;
    @Column(name = "file_path")
    private String filePath;
    @Lob
    @Column(name = "pdf_data")
    private byte[] pdfData;
    private boolean isEbook;
    
    @Transient
    private boolean isLent;

    @Transient
    public byte[] getPdfData() {
        return pdfData;
    }

    public void setPdfData(byte[] pdfData) {
        this.pdfData = pdfData;
    }

	public void setIsEbook(boolean isEbook) {
		this.isEbook=isEbook;
		
	}
    // Getter for is_lent
    public boolean islent(boolean islent) {
        return islent;
    }

    // Setter for is_lent
    public void setislent(boolean islent) {
        this.isLent = islent;
    }
    // Getter and Setter for filePath
public String getFilePath() {
    return filePath;
}

public void setFilePath(String filePath) {
    this.filePath = filePath;
}
    }