let cursor = null;
let isLoading = false;
const productContainer = document.getElementById('product-container');
const loading = document.getElementById('loading');

function toggleCategoryMenu() {
    var menu = document.getElementById("categoryMenu");
    if (menu.style.display === "none" || menu.style.display === "") {
        menu.style.display = "flex";
    } else {
        menu.style.display = "none";
    }
}

function loadProducts() {
    if (isLoading) return;

    isLoading = true;
    loading.style.display = 'block';
    let url = `/api/product/v2/list?size=10&sortType=BY_DATE&productType=ALL`;
    if (cursor) {
        url += `&cursor=${cursor}`;
    }

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data && data.data && data.data.length > 0) {
                data.data.forEach(product => {
                    const productDiv = document.createElement('div');
                    productDiv.className = 'product-item';
                    productDiv.innerHTML = `
                        <div class="product-image">
                            <img src="${product.productImage}" alt="${product.productName}">
                        </div>
                        <div class="product-name">${product.productName}</div>
                        <div class="product-price">${product.price}</div>
                    `;
                    productContainer.appendChild(productDiv);
                });
                cursor = data.cursor;
                isLoading = false;
                loading.style.display = 'none';

                // 모든 데이터를 다 불러왔는지 확인
                if (!data.cursor || data.data.length < 10) {
                    window.removeEventListener('scroll', handleScroll);
                }
            } else {
                // 더 이상 로드할 데이터가 없는 경우
                window.removeEventListener('scroll', handleScroll);
                isLoading = false;
                loading.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error loading products:', error);
            isLoading = false;
            loading.style.display = 'none';
        });
}

function handleScroll() {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
        loadProducts();
    }
}

window.addEventListener('scroll', handleScroll);
window.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});
