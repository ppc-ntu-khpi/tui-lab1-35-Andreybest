/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mybank.reporting;

import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;

/**
 * Extended CustomerReport class wth method that returns report as string instead of printing to terminal
 * @author Andrii Kotliar
 */
public class ExtendedCustomerReport extends CustomerReport {
    
    public String generateStringReport() {
        StringBuilder sb = new StringBuilder();
        // Print report header
        sb.append("CUSTOMERS REPORT");
        sb.append("================");

        // For each customer...
        for ( int cust_idx = 0;
              cust_idx < Bank.getNumberOfCustomers();
              cust_idx++ ) {
          Customer customer = Bank.getCustomer(cust_idx);

          // Print the customer's name
          sb.append("\n");
          sb.append("Customer: ").append(customer.getLastName()).append(", ").append(customer.getFirstName()).append("\n");

          // For each account for this customer...
          for ( int acct_idx = 0;
                acct_idx < customer.getNumberOfAccounts();
                acct_idx++ ) {
            Account account = customer.getAccount(acct_idx);
            String  account_type = "";

            // Determine the account type
            if ( account instanceof SavingsAccount ) {
              account_type = "Savings Account";
            } else if ( account instanceof CheckingAccount ) {
              account_type = "Checking Account";
            } else {
              account_type = "Unknown Account Type";
            }

            // Print the current balance of the account
            sb.append("    ").append(account_type).append(": current balance is ").append(account.getBalance()).append("\n");
          }
        }
        return sb.toString();
    }
    
}
