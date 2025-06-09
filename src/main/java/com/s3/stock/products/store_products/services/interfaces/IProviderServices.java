package com.s3.stock.products.store_products.services.interfaces;

import java.util.List;

import com.s3.stock.products.store_products.entitis.Provider;
import com.s3.stock.products.store_products.entitis.dto.ProviderResponseDto;

public interface IProviderServices {
    public void saveProvider(Provider provider);
    public void updateProvider(Provider provider);
    public void deleteProvider(Long id);
    public List<ProviderResponseDto> getProviders();
    public ProviderResponseDto getProviderById(Long id);
    public ProviderResponseDto getProviderByName(String name);
}
