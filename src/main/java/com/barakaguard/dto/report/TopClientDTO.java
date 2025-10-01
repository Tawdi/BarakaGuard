package main.java.com.barakaguard.dto.report;

import java.util.UUID;

public record TopClientDTO(UUID clientId, String nom, String email, double totalBalance) {

}
