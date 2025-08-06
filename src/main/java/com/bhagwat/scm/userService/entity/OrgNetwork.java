package com.bhagwat.scm.userService.entity;
import com.bhagwat.scm.userService.constants.NetworkType;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "org_networks")
public class OrgNetwork {


        @Id
        @Column(name = "network_id", unique = true, nullable = false, length = 50)
        private String networkId;

        @Column(name = "network_name", nullable = false, length = 255)
        private String networkName;

        @Column(name = "network_type")
        private NetworkType networkType;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;

        @ManyToMany(mappedBy = "networks", fetch = FetchType.LAZY)
        private Set<Org> organizations = new HashSet<>();

        public OrgNetwork() {}

        public OrgNetwork(String networkId, String networkName, String description) {
            this.networkId = networkId;
            this.networkName = networkName;
            this.description = description;
        }

        public String getNetworkId() { return networkId; }
        public void setNetworkId(String networkId) { this.networkId = networkId; }
        public String getNetworkName() { return networkName; }
        public void setNetworkName(String networkName) { this.networkName = networkName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Set<Org> getOrganizations() { return organizations; }
        public void setOrganizations(Set<Org> organizations) {
            this.organizations.clear();
            if (organizations != null) this.organizations.addAll(organizations);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrgNetwork that = (OrgNetwork) o;
            return Objects.equals(networkId, that.networkId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(networkId);
        }

        @Override
        public String toString() {
            return "OrgNetwork{" +
                    "networkId='" + networkId + '\'' +
                    ", networkName='" + networkName + '\'' +
                    '}';
        }
    }