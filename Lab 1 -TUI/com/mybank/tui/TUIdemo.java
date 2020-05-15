package com.mybank.tui;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

import com.mybank.data.DataSource;
import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.OverDraftAmountException;
import com.mybank.domain.SavingsAccount;
import com.mybank.reporting.ExtendedCustomerReport;

/**
 *
 * @author Andrii Kotliar and Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;
    private static final int CUST_REPO = 2020;
    
    private boolean successfullFileLoad = true;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addItem(CUST_REPO, "&Customer Report");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        
        try {
            DataSource dataSource = new DataSource("data/test.dat");
            dataSource.loadData();
        } catch(Exception exception) {
            successfullFileLoad = false;
        }
        
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Banking App.\n\nCopyright \u00A9 2020 Andrii Kotliar (AndreyBest)\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        if (menu.getId() == CUST_REPO) {
            ShowCustomerReport();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowAccountDetails(Account account) {
        TWindow accountDetailsWindow = addWindow("Account Info Window", 2, 1, 30, 8, TWindow.NOZOOMBOX);
        accountDetailsWindow.newStatusBar("Here you can see all account info...");
        
        TText details = accountDetailsWindow.addText("", 2, 2, 28, 8);
        StringBuilder sb = new StringBuilder().append("Account Type: ");
        if (account instanceof SavingsAccount) {
            sb.append("'Savings'\n");
        } else if (account instanceof CheckingAccount) {
            sb.append("'Checking'\n");
        } else {
            sb.append("'Unknown'\n");
        }
        sb.append("Account Balance: $").append(account.getBalance());
        details.setText(sb.toString());
    }
    
    private void ShowAccountDeposit(Account account) {
        TWindow accountDepositWindow = addWindow("Account Deposit Window", 2, 1, 40, 8, TWindow.NOZOOMBOX);
        accountDepositWindow.newStatusBar("Enter the amount you need to deposit and press Deposit...");
        
        accountDepositWindow.addLabel("Deposit amount: ", 2, 2);
        TField depositAmountField = accountDepositWindow.addField(18, 2, 18, false);
        accountDepositWindow.addButton("&Deposit", 15, 4, new TAction() {
            @Override
            public void DO() {
                try {
                    int depositAmount = Integer.parseInt(depositAmountField.getText());
                    account.deposit(depositAmount);
                    messageBox("Success!", "Successfully deposited $" + depositAmount);
                } catch (NumberFormatException e) {
                    messageBox("Error", "Enter correct number!").show();
                } catch (Exception e) {
                    messageBox("Error", "Some mystic error happened!").show();
                }
            }
        });
    }
    
    private void ShowAccountWithdraw(Account account) {
        TWindow accountWithdrawWindow = addWindow("Account Withdraw Window", 2, 1, 40, 8, TWindow.NOZOOMBOX);
        accountWithdrawWindow.newStatusBar("Enter the amount you need to withdraw and press Withdraw...");
        
        accountWithdrawWindow.addLabel("Withdraw amount: ", 2, 2);
        TField withdrawAmountField = accountWithdrawWindow.addField(19, 2, 17, false);
        accountWithdrawWindow.addButton("&Withdraw", 14, 4, new TAction() {
            @Override
            public void DO() {
                try {
                    int withdrawAmount = Integer.parseInt(withdrawAmountField.getText());
                    account.withdraw(withdrawAmount);
                    messageBox("Success!", "Successfully withdrawed $" + withdrawAmount);
                } catch(OverDraftAmountException e) {
                    messageBox("Error", "Couldn't withdraw that amount!").show();
                } catch (NumberFormatException e) {
                    messageBox("Error", "Enter correct number!").show();
                } catch (Exception e) {
                    messageBox("Error", "Some mythtic error happened!").show();
                }
            }
        });
    }
    
    private void ShowCustomerDetails() {    
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 33, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show, then enter account index and choose option to do with chosen account...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        
        custWin.addLabel("Enter account index: ", 2, 4);
        TField accountIndexField = custWin.addField(24, 4, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 6, 38, 18);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    if (successfullFileLoad == false) {
                        messageBox("Error", "File with customer details wasn't found.").show();
                        return;
                    } 
                    int custNum = Integer.parseInt(custNo.getText());
                    Customer customer = Bank.getCustomer(custNum);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Owner Name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(" (id=").append(custNum).append(")\n");
                    for (int i = 0; i < customer.getNumberOfAccounts(); i++) {
                        sb.append("Account Type: ");
                        Account account = customer.getAccount(i);
                        if (account instanceof SavingsAccount) sb.append("'Savings'\n");
                        else if (account instanceof CheckingAccount) sb.append("'Checking'\n");
                        else sb.append("'Unknown'\n");
                        sb.append("Account Balance: $").append(account.getBalance()).append("\n");
                    }
                    if (customer.getNumberOfAccounts() < 1) sb.append("No accounts");
                    details.setText(sb.toString());
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
        
        custWin.addButton("&Show Account Details", 8, 25, new TAction() {
            @Override
            public void DO() {
                try {
                    if (successfullFileLoad == false) {
                        messageBox("Error", "File with customer details wasn't found.").show();
                        return;
                    } 
                    int customerNumber = Integer.parseInt(custNo.getText());
                    int accountIndex = Integer.parseInt(accountIndexField.getText());
                    Customer customer = Bank.getCustomer(customerNumber);
                    ShowAccountDetails(customer.getAccount(accountIndex));
                } catch (Exception e) {
                    messageBox("Error", "Couldn't find customer or his/her account.").show();
                }
            }
        });
        
        custWin.addButton("&Deposit to Account", 9, 27, new TAction() {
            @Override
            public void DO() {
                try {
                    if (successfullFileLoad == false) {
                        messageBox("Error", "File with customer details wasn't found.").show();
                        return;
                    } 
                    int customerNumber = Integer.parseInt(custNo.getText());
                    int accountIndex = Integer.parseInt(accountIndexField.getText());
                    Customer customer = Bank.getCustomer(customerNumber);
                    ShowAccountDeposit(customer.getAccount(accountIndex));
                } catch (Exception e) {
                    messageBox("Error", "Couldn't find customer or his/her account.").show();
                }
            }
        });
        
        custWin.addButton("&Withdraw from Account", 8, 29, new TAction() {
            @Override
            public void DO() {
                try {
                    if (successfullFileLoad == false) {
                        messageBox("Error", "File with customer details wasn't found.").show();
                        return;
                    } 
                    int customerNumber = Integer.parseInt(custNo.getText());
                    int accountIndex = Integer.parseInt(accountIndexField.getText());
                    Customer customer = Bank.getCustomer(customerNumber);
                    ShowAccountWithdraw(customer.getAccount(accountIndex));
                } catch (Exception e) {
                    messageBox("Error", "Couldn't find customer or his/her account.").show();
                }
            }
        });
    }
    
    private void ShowCustomerReport() {    
        TWindow customerReportWindow = addWindow("Customer Report Window", 2, 1, 80, 40, TWindow.NOZOOMBOX);
        customerReportWindow.newStatusBar("Press 'Show Customer Report' to get the customer report...");
        
        TText details = customerReportWindow.addText("Here will be customer info after you press the button", 2, 3, 78, 38);
        
        customerReportWindow.addButton("&Show Customer Report", 28, 1, new TAction() {
            @Override
            public void DO() {
                try {
                    if (successfullFileLoad == false) {
                        messageBox("Error", "File with customer details wasn't found.").show();
                        return;
                    }
                    ExtendedCustomerReport customerReport = new ExtendedCustomerReport();
                    details.setText(customerReport.generateStringReport());
                } catch (Exception e) {
                    messageBox("Error", "Sore serious error happened!").show();
                }
            }
        });
    }
}
