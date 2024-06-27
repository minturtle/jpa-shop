let cursor = null;
let isLoading = false;
let sortType = 'BY_DATE';
let query = '';
let category = ''
let minPrice = ''
let maxPrice = ''

const productContainer = document.getElementById('product-container');
const loading = document.getElementById('loading');


document.getElementById('sort-select').addEventListener('change', (event) => {
    sortType = event.target.value;
    cursor = null;  // Reset cursor when changing sort type
    productContainer.innerHTML = '';  // Clear previous products
    loadProducts();
});


function toggleCategoryMenu() {
    var menu = document.getElementById("categoryMenu");
    if (menu.style.display === "none" || menu.style.display === "") {
        menu.style.display = "flex";
    } else {
        menu.style.display = "none";
    }
}

function handleSearch() {
    query = document.getElementById('search-input').value;
    minPrice = document.getElementById('min-price').value;
    maxPrice = document.getElementById('max-price').value;
    cursor = null;  // Reset cursor when performing a new search
    productContainer.innerHTML = '';  // Clear previous products
    loadProducts();
}

function resetSearch() {
    document.getElementById('search-input').value = '';
    document.getElementById('min-price').value = '';
    document.getElementById('max-price').value='';

    query = ''
    category = ''
    cursor = null;  // Reset cursor when changing category
    productContainer.innerHTML = '';
    minPrice = ''
    maxPrice = ''
    removeCategorySelect()
    loadProducts()
}
function loadProducts() {
    if (isLoading) return;

    isLoading = true;
    loading.style.display = 'block';
    let url = `/api/product/v2/list?size=10&sortType=${sortType}&productType=ALL`;
    if (cursor) {
        url += `&cursor=${cursor}`;
    }
    if (query) {
        url += `&query=${encodeURIComponent(query)}`;
    }
    if(category){
        url += `&category=${category}`
    }
    if(minPrice !== ''){
        url += `&minPrice=${minPrice}`
    }
    if(maxPrice !== ''){
        url += `&maxPrice=${maxPrice}`
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

                    productDiv.addEventListener('click', () => {
                        window.location.href = `/api/product/${product.productUid}`;
                    });
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

function handleCategoryClick(currentCategory) {
    category = currentCategory;
    cursor = null;  // Reset cursor when changing category
    productContainer.innerHTML = '';  // Clear previous products
    loadProducts();
}

function handleScroll() {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
        loadProducts();
    }
}

function removeCategorySelect(){
    // 모든 카테고리 아이템에서 'selected' 클래스 제거
    document.querySelectorAll('.category-item').forEach(i => {
        i.classList.remove('selected');
    });
}

window.addEventListener('scroll', handleScroll);
window.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});
document.querySelectorAll('.category-item').forEach(item => {
    item.addEventListener('click', event => {
        const category = event.target.getAttribute('id');
        handleCategoryClick(category);

        removeCategorySelect();
        // 선택된 아이템에 'selected' 클래스 추가
        event.target.classList.add('selected');

    });
});

