package me.invakid.azran.entity;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class PayPalClient {

    private String clientId = "Afr7XiTw1a0THYtVf97KqyF1-8cboBjMjrN3PLIcI5ppy1yTl3T0Hfe2K6v0MdocHYtzIT9KJcQq2C6n";
    private String clientSecret = "EGQ1XOD6cUEUokvTXjXOL-E92VVjN6A3ENtpVjG1K81jglIQDIjsxfcWSaeFNTwWkCNcJF0mpC5jIE9l";

    public String createPayment(String sum, String ticketId, String ticketName) {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("https://azran.info/");
        redirectUrls.setReturnUrl("https://azran.info/complete/payment/" + ticketId + "?ticketName=" + ticketName);
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;
        String redirectUrl = "";
        try {
            APIContext context = new APIContext(clientId, clientSecret, "live");
            createdPayment = payment.create(context);
            if (createdPayment != null) {
                List<Links> links = createdPayment.getLinks();
                for (Links link : links) {
                    if (link.getRel().equals("approval_url")) {
                        redirectUrl = link.getHref();
                        break;
                    }
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error happened during payment creation!");
        }
        return redirectUrl;
    }

    public String completePayment(HttpServletRequest req) {
        Payment payment = new Payment();
        payment.setId(req.getParameter("paymentId"));

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(req.getParameter("PayerID"));
        try {
            APIContext context = new APIContext(clientId, clientSecret, "live");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if (createdPayment != null) {
                String email = createdPayment.getPayer().getPayerInfo().getEmail();
                String price = createdPayment.getTransactions().get(0).getAmount().getTotal();
                return String.format("success:::%s:::%s", email, price);
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
        return "fail";
    }

}