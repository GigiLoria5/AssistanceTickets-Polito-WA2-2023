package it.polito.wa2.g29.server.service.impl

import it.polito.wa2.g29.server.dto.ProductDTO
import it.polito.wa2.g29.server.dto.TokenDTO
import it.polito.wa2.g29.server.dto.toDTO
import it.polito.wa2.g29.server.exception.*
import it.polito.wa2.g29.server.model.ProductToken
import it.polito.wa2.g29.server.repository.ProductRepository
import it.polito.wa2.g29.server.repository.ProductTokenRepository
import it.polito.wa2.g29.server.repository.ProfileRepository
import it.polito.wa2.g29.server.service.ProductService
import it.polito.wa2.g29.server.utils.AuthenticationUtil
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service

class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productTokenRepository: ProductTokenRepository,
    private val profileRepository: ProfileRepository
) : ProductService {

    private val log = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    override fun getAllProducts(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProductById(productId: Int): ProductDTO {
        val product = productRepository.findByIdOrNull(productId)
            ?: run {
                log.info("Product not found")
                throw ProductNotFoundException()
            }
        return product.toDTO()
    }

    override fun generateProductToken(productId: Int): TokenDTO {
        val product = productRepository.findByIdOrNull(productId)
            ?: run {
                log.info("Product not found")
                throw ProductNotFoundException()
            }
        val productToken = ProductToken(product)
        productTokenRepository.save(productToken)
        return TokenDTO(productToken.token)
    }

    @Transactional
    override fun registerProduct(token: String) {
        //controlla di avere token
        val productToken = productTokenRepository.findProductTokenByToken(token)
            ?: run {
                log.info("Product token not found")
                throw ProductTokenNotFoundException()
            }
        //controlla che non sia usato
        if(productToken.user!=null)
            throw TokenAlreadyUsedException("Token already used")
        //associa user a token
        val username = AuthenticationUtil.getUsername()
        val customer = profileRepository.findProfileByEmail(username)!!

        productToken.user=customer
        productTokenRepository.save(productToken)
    }

}
