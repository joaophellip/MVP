package com.example

import com.google.gson.Gson
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomerProfileWithFundingInstrumentSerialTest{

    var customerProfile = com.cozo.cozomvp.payment.CustomerProfileWithFundingInstrument(
            "meu_id_customer_002",
            "Maria",
            "maria@email.com",
            "1996-09-26",
            "CPF",
            "10013390023",
            "55",
            "11",
            "22226842",
            "Rio de Janeiro",
            "Ipanema",
            "Avenida Atlântica",
            "60",
            "02446000",
            "RJ",
            "BRA"
    )

    val expected = "{\"ownId\":\"meu_id_customer_002\",\"fullname\":\"Maria\",\"email\":\"maria@email.com\",\"birthDate\":\"1996-09-26\",\"taxDocument\":{\"type\":\"CPF\",\"number\":\"10013390023\"},\"phone\":{\"countryCode\":\"55\",\"areaCode\":\"11\",\"number\":\"22226842\"},\"shippingAddress\":{\"city\":\"Rio de Janeiro\",\"district\":\"Ipanema\",\"street\":\"Avenida Atlântica\",\"streetNumber\":\"60\",\"zipCode\":\"02446000\",\"state\":\"RJ\",\"country\":\"BRA\"},\"fundingInstrument\":{\"creditCard\":{\"holder\":{\"fullname\":\"Maria Oliveira\",\"birthdate\":\"1996-09-26\",\"taxDocument\":{\"type\":\"CPF\",\"number\":\"10013390023\"},\"phone\":{\"countryCode\":\"55\",\"areaCode\":\"11\",\"number\":\"22226842\"}}}}}"

    @Test
    fun generateJSON_success(){
        var gson = Gson()
        val result = gson.toJson(customerProfile)
        Assert.assertEquals(result,expected)
    }
}