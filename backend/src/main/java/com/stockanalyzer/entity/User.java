package com.stockanalyzer.entity;

import com.stockanalyzer.entity.enums.Role;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity extending BaseUser — demonstrates INHERITANCE.
 * Encapsulates user data with role-based access.
 */
@Entity
@Table(name = "users")
public class User extends BaseUser {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INVESTOR;

    @Column(precision = 15, scale = 2)
    private BigDecimal virtualBalance = new BigDecimal("100000.00");

    @Column(precision = 15, scale = 2)
    private BigDecimal totalInvested = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalProfit = BigDecimal.ZERO;

    private String profilePicture;

    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Portfolio> portfolios = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Watchlist> watchlists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alert> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public User() {}

    public User(String firstName, String lastName, String email, String password, Role role) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        this.role = role;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public BigDecimal getVirtualBalance() { return virtualBalance; }
    public void setVirtualBalance(BigDecimal virtualBalance) { this.virtualBalance = virtualBalance; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(List<Portfolio> portfolios) { this.portfolios = portfolios; }

    public List<Watchlist> getWatchlists() { return watchlists; }
    public void setWatchlists(List<Watchlist> watchlists) { this.watchlists = watchlists; }

    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
