package com.s3.stock.products.store_products.services;

import java.util.List;
import java.util.Optional;

import org.aspectj.apache.bcel.classfile.Module.Provide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.s3.stock.products.store_products.entitis.Provider;
import com.s3.stock.products.store_products.entitis.dto.ProviderResponseDto;
import com.s3.stock.products.store_products.repositories.IProviderRepository;
import com.s3.stock.products.store_products.services.interfaces.IProviderServices;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProviderServices implements IProviderServices {

    @Autowired
    private IProviderRepository providerRepository;

    @Override
    public void deleteProvider(Long id) {
        Optional<Provider> provider = providerRepository.findById(id);
        if(provider.isEmpty()){
            throw new EntityNotFoundException("Provider not exist");
        }

        providerRepository.deleteById(id);
    }

    @Override
    public ProviderResponseDto getProviderById(Long id) {
        return convertProviderToProviderResponseDto(providerRepository.findById(id).orElseThrow( ()-> new EntityNotFoundException("Provider not exist")));
    }

    @Override
    public ProviderResponseDto getProviderByName(String name) {
        return convertProviderToProviderResponseDto(providerRepository.findByName(name).get());
    }

    @Override
    public List<ProviderResponseDto> getProviders() {
        return providerRepository.findAll().stream().map(provider -> convertProviderToProviderResponseDto(provider)).toList();
    }

    @Override
    public void saveProvider(Provider provider) {
        Optional<Provider> providerExist = providerRepository.findByName(provider.getName());
        if(providerExist != null){
            throw new EntityNotFoundException("Provider already exist");
        }

        providerRepository.save(provider);
    }

    @Override
    public void updateProvider(Provider provider) {
        
        Optional<Provider> providerOptional = providerRepository.findById(provider.getId());
        if(providerOptional.isPresent()) {
            Provider providerUpdate = providerOptional.get();
            providerUpdate.setName(provider.getName());
            providerUpdate.setContact(provider.getContact());
            providerUpdate.setAddres(provider.getAddres());
            providerUpdate.setProducts(provider.getProducts());
            providerRepository.save(providerUpdate);
        }else{
            throw new EntityNotFoundException("Provider not exist");
        }
    }


    private ProviderResponseDto convertProviderToProviderResponseDto(Provider provider) {

            ProviderResponseDto providerResponseDto = new ProviderResponseDto();
            providerResponseDto.setId(provider.getId());
            providerResponseDto.setName(provider.getName());
            providerResponseDto.setContact(provider.getContact());
            return providerResponseDto;
    }

    
}
