package com.sigga.ecommerce.order.purchase;

import com.sigga.ecommerce.core.service.EcommerceService;
import com.sigga.ecommerce.crm.customer.CustomerResourceClient;
import com.sigga.ecommerce.inventory.product.ProductResourceClient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderService extends EcommerceService<PurchaseOrderEntity, PurchaseOrder> {

    private final CustomerResourceClient customerClient;

    private final ProductResourceClient productResourceClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repository,
            ModelMapper modelMapper,
            CustomerResourceClient customerClient,
            ProductResourceClient productResourceClient) {

        super(repository, modelMapper);

        this.customerClient = customerClient;
        this.productResourceClient = productResourceClient;
    }

    @Override
    protected PurchaseOrder mapEntityToValueObject(PurchaseOrderEntity entity) {

        var purchase = super.mapEntityToValueObject(entity);

        purchase.setCustomer(this.customerClient.findById(entity.getCustomerId()));

        purchase.getProducts().forEach(purch -> {
            var product = this.productResourceClient.findById(purch.getProduct().getId());
            purch.getProduct().setName(product.getName());

            entity.getProducts().stream().filter(prod->prod.getProductId().equals(product.getId())).forEach(
                    en -> purch.getProduct().setPrice(en.getPrice())
            );

        });

        return purchase;
    }

    @Override
    protected PurchaseOrderEntity mapValueObjectToEntity(PurchaseOrder valueObject) {

        var purchase = super.mapValueObjectToEntity(valueObject);

        purchase.getProducts().forEach(purch -> purch.setPrice(this.productResourceClient.findById(purch.getProductId()).getPrice()));

        return purchase;
    }
}
