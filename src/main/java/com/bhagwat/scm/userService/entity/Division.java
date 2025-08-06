package com.bhagwat.scm.userService.entity;

import com.bhagwat.scm.userService.constants.PartyType;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "divisions")
public class Division {

        @Id
        @Column(name = "division_id", unique = true, nullable = false, length = 50)
        private String divisionId;

        @Column(name = "division_name", nullable = false, length = 255)
        private String divisionName;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;

        @Column(name = "part_type")
        private PartyType PartyType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "org_id", referencedColumnName = "org_id", nullable = false)
        private Org org;

        public Division() {}

        public Division(String divisionId, String divisionName, String description, Org org) {
            this.divisionId = divisionId;
            this.divisionName = divisionName;
            this.description = description;
            this.org = org;
        }

        public String getDivisionId() { return divisionId; }
        public void setDivisionId(String divisionId) { this.divisionId = divisionId; }
        public String getDivisionName() { return divisionName; }
        public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Org getOrg() { return org; }
        public void setOrg(Org org) { this.org = org; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Division division = (Division) o;
            return Objects.equals(divisionId, division.divisionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(divisionId);
        }

        @Override
        public String toString() {
            return "Division{" +
                    "divisionId='" + divisionId + '\'' +
                    ", divisionName='" + divisionName + '\'' +
                    '}';
        }
    }