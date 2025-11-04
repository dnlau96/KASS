/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.beans;

/**
 *
 * @author dnlau
 */

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import lau.kass2.models.Sale;
import lau.kass2.services.SaleService;

@Named("reportBean")
@ViewScoped
public class ReportBean implements Serializable {

    @Inject
    private SaleService saleService;

    private List<Sale> salesList;
    
 
    @PostConstruct
    public void init() {
        // Carga todas las ventas cuando la página se abre
        loadAllSales();
    }

    public void loadAllSales() {
        this.salesList = saleService.getAllSales();
    }
    
  

    public List<Sale> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<Sale> salesList) {
        this.salesList = salesList;
    }
}